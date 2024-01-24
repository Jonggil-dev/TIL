# React-Router-Dom

### 1. 사용법

```react
     <BrowserRouter>
            <Routes>
                	//일반적인 Route 사용법
                  <Route path="/" element={<Home />} />
                  
                	//PrivateRoute + requireAuth 속성을 통해 로그인 여부 판단
                	// 로그인이 됐을 시 /shop 으로 이동하여 <shop/> 컴포넌트 렌더링
                  <Route element={<PrivateRoute requireAuth={true} />}>
                    <Route path="/shop" element={<Shop />} />
                  </Route>
                
                  <Link to="/shop">shop 페이지로 이동</Link>
            </Routes>
      </BrowserRouter>
```



### 2. 설명

- `<BrowserRouter>` : 브라우저 기반의 라우팅을 지원

- `<Routes>` : 라우트 구성을 정의하는 컴포넌트 routing 요청이 오면 routes 내부에 있는 태그 중에서 해당 Route를 찾음
- `<Route path="/shop" element={<Shop />} />` : `/shop` 경로에 대한 라우트를 정의하면서, 해당 경로로 이동할 때 `<Shop>` 컴포넌트를 렌더링
- `<Route element={<PrivateRoute requireAuth={true} />}>`: `PrivateRoute` 컴포넌트는 사용자의 인증 상태를 확인하여 필요에 따라 리다이렉트하거나 특정 컴포넌트를 렌더링하는 역할

- `<Link to="/shop">'` : /about url로 이동. <Route>와의 차이점은 **Link는 그냥 해당 url로 이동만 시켜주는 거임** -> 예시로 `<Route path="/shop" element={<Shop />} />` 가 되어 있는 상태에서`<Link to="/shop">`을  클릭하면 url이 /shop으로 이동하게 되고 <Route>에 태그에 적혀있는 대로 실행 되는거임