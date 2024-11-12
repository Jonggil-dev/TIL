# Java List, Queue, Deque 정리

```
Collection (인터페이스)
    ├── List (인터페이스) : 순서 유지, 인덱스 기반
    │   ├── ArrayList (클래스)
    │   ├── LinkedList (클래스) (List와 Queue, Deque를 모두 구현)
    │   ├── Vector (클래스)
    │   │   └── Stack (클래스)
    │
    ├── Queue (인터페이스) : FIFO 구조
    │   ├── LinkedList (클래스) (Queue와 List, Deque를 모두 구현)
    │   ├── PriorityQueue (클래스)
    │   ├── ArrayDeque (클래스) (Deque도 구현)
    │
    └── Deque (인터페이스) : 양방향 삽입/제거 가능
        ├── LinkedList (클래스) (Deque와 List, Queue를 모두 구현)
        ├── ArrayDeque (클래스)

```

- **`List`**는 **인덱스 기반**의 순서가 있는 데이터 집합을 구현하기 위한 인터페이스
- **`Queue`**는 FIFO 방식의 전형적인 Queue를 구현하기 위한 인터페이스
- **`Deque`**는 스택(LIFO)과 큐(FIFO)의 기능을 모두 가진 자료 구조를 구현하기 위한 인터페이스