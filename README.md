# Can AI Catch Bugs Your Compiler Can't?

Live-demo code for the HackersMang #HMSep26 talk. Every bug here compiles
cleanly and has a "happy path" JUnit test that passes — that's the point.
Each package is self-contained, so you can present them in any order and
skip whichever ones you're short on time for.

```
race/           Demo 1 — a race condition, fixed with an atomic type
txproxy/        Demo 2 — @Transactional silently bypassing its own proxy
nplusone/       Demo 3 — the N+1 query problem
silentfailure/  Bonus  — an exception swallowed with no trace anywhere
```

## Running it

```bash
mvn spring-boot:run
```

Starts on `http://localhost:8080`. `nplusone` seeds 200 orders with 3
items each on startup — that's the dataset the N+1 demo uses.

To run one test class on its own (this is how you should run each demo
live, so the console output stays readable):

```bash
mvn -Dtest=CounterConcurrencyTest test
```

---

## Demo 1 — race condition (`race/`)

**The setup:** `CounterService.increment()` does `count = count + 1` with
no lock, no `synchronized`, no atomic type.

**Show it passing:** `CounterServiceTest` — 1000 sequential increments,
single thread, asserts the count is exactly 1000. Green.

**Show it failing:** `CounterConcurrencyTest` — 10 threads × 1000
increments each, expects 10,000, almost always comes up short.

```bash
mvn -Dtest=CounterConcurrencyTest test
```

If it happens to pass on the first try (it can — races are races), run it
again on stage. That's an honest part of the demo: "it passed once" was
never proof of correctness.

**The fix:** swap the field in `CounterService` for the commented-out
`AtomicInteger` block, re-run `CounterConcurrencyTest`, watch it go green
reliably.

---

## Demo 2 — `@Transactional` bypassing its own proxy (`txproxy/`)

**The setup:** `OrderService.placeOrder()` calls `this.saveOrder(order)`.
`saveOrder()` is `@Transactional` and is meant to roll back the save if
the payment charge inside it fails. Because it's called via `this` — a
self-invocation — Spring's proxy is never involved, so `@Transactional`
never activates. No exception for this, no warning at startup.

**Show it passing:** `OrderServiceTest` — a normal order, no payment
failure, nothing to roll back. Green either way, bug or no bug.

**Show it failing:** `OrderServiceRollbackTest` — an order over 1000 fails
the simulated payment gateway. The save should roll back with it;
`repository.count()` should be 0. It isn't.

```bash
mvn -Dtest=OrderServiceRollbackTest test
```

Or hit it over HTTP while the app is running:

```bash
curl -X POST localhost:8080/orders -H "Content-Type: application/json" \
  -d '{"customerName":"Ravi","amount":5000}'
# -> 500 Internal Server Error (payment declined)

curl localhost:8080/orders
# -> Ravi's order is in there anyway
```

**The fix:** stop self-invoking — either have the caller go through the
Spring-managed bean instead of `this`, or split the class so the
transactional method is always reached from outside. Notes on both are in
`OrderService.java`.

---

## Demo 3 — the N+1 query problem (`nplusone/`)

**The setup:** `totalRevenueBuggy()` calls `repository.findAll()`, then
loops and calls `order.getItems()` on each one. Each `.getItems()` is a
separate lazy-load query. 200 orders → 201 queries for what should be one.

**Show it "passing":** the return value is correct either way — N+1 is a
performance bug, not a correctness bug, so an assertion on the *result*
was never going to catch it.

**Show the query count, which is the real story:**

```bash
mvn -Dtest=OrderRepositoryTest test
```

`OrderRepositoryTest` uses Hibernate's `Statistics` API to count queries
exactly, and asserts on that count — so the "buggy" test method prints
201 queries and fails an assertion of `1`, and the "fixed" one (using
`findAllWithItems()`, a `JOIN FETCH` query) prints 1 and passes.

Or watch it live with SQL logging (already on in `application.properties`):

```bash
curl localhost:8080/nplusone/revenue          # console floods with queries
curl localhost:8080/nplusone/revenue-fixed    # console prints exactly one
```

**The fix:** `OrderRepository.findAllWithItems()` — a `JOIN FETCH` query —
already exists; `totalRevenueFixed()` uses it. Point `totalRevenueBuggy()`
at it and the console goes quiet.

---

## Bonus — showing a "silently broken, no exceptions" bug (`silentfailure/`)

This is the pattern behind "Unhandled exceptions" on the premise slide,
and it's worth a slide of its own if you have room: code that never
throws — checked or unchecked — and still doesn't do what it claims.

**The setup:** `NotificationService.sendOrderConfirmation()` tries to send
an email, catches the exception if the provider fails, and returns `true`
regardless. The method signature promises nothing can go wrong. It's lying.

**How to demo "looks fine, breaks silently" in general — the technique,
not just this one example:**

1. **Call it and show the response looks completely normal.** No stack
   trace, no error, no red anywhere.

   ```bash
   curl -X POST localhost:8080/notify -H "Content-Type: application/json" \
     -d '{"email":"ravi@bounce.test","orderId":"A101"}'
   # -> "sent: true"
   ```

2. **Then check independent ground truth** — a side effect the caller
   never sees, but that tells you what actually happened.

   ```bash
   curl localhost:8080/notify/sent-log
   # -> [] (empty — nothing was ever sent)
   ```

3. **Run `NotificationServiceTest.reportsSuccessEvenWhenDeliveryFails()`
   live.** Both assertions in it pass: the method really did claim
   success, AND zero emails really were sent. Two true statements about
   the same call that shouldn't both be true — that contradiction, stated
   out loud, is the whole demo. You don't need a failing test to make a
   silent-failure bug land; the absence of a failing test *is* the bug.

4. **Ask the AI to review just this method** (paste `NotificationService`
   on its own, without the comments giving it away) and see whether it
   flags the empty/near-empty catch block and the fixed return value.
   This is a good one to prompt generically first ("review this method")
   and then specifically ("what happens to callers when `emailGateway.send`
   throws?") — showing the specific prompt finding what the generic one
   missed is worth the extra 30 seconds on stage.

**Other shapes this same bug takes**, worth mentioning even if you don't
code them all up:
- a caught exception that's logged at `debug` level instead of `warn`/`error`,
  so it exists in theory but nobody's alert ever fires on it
- an ignored return value that signals failure (`List.remove()`,
  `File.delete()`, a `boolean` result from a legacy API)
- an `@Async` void method whose exception Spring swallows by default
  unless you configure an `AsyncUncaughtExceptionHandler`
- `Optional.orElse(null)` turning a real absence into a silent `null`
  three calls downstream

**The fix:** shown as comments in `NotificationService.java` — either let
the exception propagate, or catch it and return/report the real outcome
instead of a hardcoded one.

---

## Suggested run order on stage

1. `race/` — timing-dependent, so demo it early while you still have a
   fresh audience and slack in the schedule if the repro needs a retry.
2. `txproxy/` — the "aha" moment; ask the AI to explain *why*, not just
   *that*.
3. `nplusone/` — easiest to prove, good if you're rebuilding momentum.
4. `silentfailure/` — closes the loop back to the "unhandled exceptions"
   line on the premise slide.
