import http from "k6/http";
import { check, sleep, group } from "k6";
import exec from "k6/execution";

export let options = {
  stages: [
    { duration: "30s", target: 10 }, // Ramp-up to 20 VUs
    { duration: "1m", target: 20 }, // Stay at 20 VUs for 1 minute
  ],
};

export default function () {
  const URL = "http://localhost:8080/health";

  group("api gateway endpoint", function () {
    let id = exec.scenario.iterationInTest;
    console.log(`Iteration: ${id}`);
    let res = http.get(URL, {
      headers: {
        "X-Idempotency-Key": id,
      },
    });
    check(res, { "status is 200": (r) => r.status === 200 });
    // console.log(id, res)

    sleep(0.01);
  });
}
