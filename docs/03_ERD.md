# ERD

<img src="https://github.com/user-attachments/assets/e164d74a-63b2-435d-b4c3-f54c45349628" style="width: 100%;" alt="Image 12 (1)">


## 설명

---

### `User` 와 `Queue`
```text
하나의 User는 여러개의 Queue를 가질 수 있다.
```

### `User` 와 `Point`
```text
하나의 User는 하나의 Point만 가질 수 있다.
```

### `User` 와 `Payment`
```text
하나의 User는 여러개의 Payment를 가질 수 있다.
```

### `User` 와 `Reservation`
```text
하나의 User는 여러개의 Reservation를 가질 수 있다.
```

### `Reservation` 와 `Payment`
```text
하나의 Reservation은 하나의 Payment를 가질 수 있다.
```

### `Concert` 와 `Reservation`
```text
하나의 Concert는 여러개의 Reservation을 가질 수 있다.
```

### `Concert` 와 `ConcertSchedule`
```text
하나의 Concert는 여러개의 ConcertSchedule을 가질 수 있다.
```

### `ConcertSchedule` 와 `Seat`
```text
하나의 ConcertSchedule은 여러개의 Seat를 가질 수 있다.
```

### `ConcertSchedule` 와 `Reservation`
```text
하나의 ConcertSchedule은 여러개의 Reservation을 가질 수 있다.
```

### `Seat` 와 `Reservation`
```text
하나의 Seat는 여러개의 Reservation을 가질 수 있다.
```
