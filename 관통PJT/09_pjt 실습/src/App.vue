<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <nav class="navbar navbar-expand-lg bg-dark navbar-expand">
    <div class="container-fluid">
      <a class="navbar-brand text-light" href="#">MyTube</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <RouterLink class="nav-link text-light" to="/">Home</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link text-light" :to="{ name: 'search' }">Search</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link text-light" :to="'later'">Later</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink class="nav-link text-light" :to="'channel'">Channel</RouterLink>
          </li>
        </ul>
      </div>
    </div>
  </nav>
      
  <RouterView class="view" />
</template>

<style scoped>
nav {
  position: fixed;
  top: 0;
  width: 100%; /* 너비를 100%로 설정하여 전체 뷰포트 너비를 채웁니다. */
  z-index: 1000; /* 다른 요소들 위에 나타나도록 z-index를 높게 설정합니다. */
}
.view{
  margin-top: 100px;
}

</style>
