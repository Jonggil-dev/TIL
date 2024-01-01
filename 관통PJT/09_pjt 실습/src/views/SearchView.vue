<template>
    <div>
      <button @click="goBack" class="btn btn-dark mb-4">← 뒤로가기</button>
      <form class="form" @submit.prevent="searchVideo(inputValue)" >
        <h1>비디오 검색</h1>
        <input type="text" v-model="inputValue">
        <button class="ms-2"> 찾기 </button>
      </form>
      
      <template v-if="productlistEmpty">
        <div class="container">
          <div v-for="video in videos.items" class="video" @click="goDetail(video)">
            <img :src="video.snippet.thumbnails.high.url" alt="thumbnail">
            <p>{{ video.snippet.title }}</p>
          </div>
        </div>
      </template>
    </div>
</template>

<script setup>
import { ref,computed } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios'
import { onMounted } from 'vue';

const inputValue = ref('')
const videos = ref(false)

const productlistEmpty = computed(() => {
    return Boolean(videos.value) ? true : false
})

const searchVideo = (inputValue)=>{
    router.push({ query: { search: inputValue } }); //다시 search 페이지로 돌아왔을 때 검색했던 영상 출력 상태를 제공하기 위해 query로 url에 반영해두기
    axios({
        url: 'https://www.googleapis.com/youtube/v3/search',
        method: 'get',
        params: {
            part: 'snippet',
            q: inputValue,
            regionCode : 'KR',
            type: 'video',
            maxResults: 25,
            key: 'AIzaSyAuexJ8PlQlJZeeFlyBmfbDCp3-LzXg980'
        }
        })
        .then((response)=>{
          videos.value = response.data
        })
        .catch((error)=>{
          console.log(error)
        })
}


const router = useRouter()
const goDetail = (video)=>{
  router.push({name:'detail', params:{ pk:video.id.videoId }})
}

//뒤로가기 구현
const goBack = () => {
    router.back()
}

//다른 페이지에서 뒤로가기로 serach페이지로 왔을때 이전에 동영상 검색결과가 있었으면 해당 화면상태를 동일하게 보여주기 위함 (serachVideo함수를 동일한 검색어로 다시 실행하는로직이라 결과가 조금 다름,, 보완필요) 
onMounted(() => {
    const query = router.currentRoute.value.query;
    if (query.search) {
        searchVideo(query.search);
    }
})
</script>

<style scoped>
.btn {
  margin-left: 30px;
}

.form {
  margin: 30px;
}
 
</style>
