import http from 'k6/http';
import { check, sleep} from 'k6';

export let options = {
    stages: [
        { duration: '1s', target: 600 },
        { duration: '2s', target: 500 },
        { duration: '2s', target: 400 },
        { duration: '2s', target: 300 },
        { duration: '3s', target: 200 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'],
        http_req_failed: ['rate<0.01'],
    },
};

export  default function(){
    let userId = 1;

    let url = 'http://localhost:8080/api/queues/token'
    let payload = JSON.stringify({
        userId: userId,
    });

    let params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let response = http.post(url, payload, params);

    check(response, {
        'status is 201': (r) => r.status === 201,
        'response time < 200ms': (r) => r.timings.duration < 200,
    });

    sleep(0.5);


}
