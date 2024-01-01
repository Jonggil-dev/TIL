<template>
    <div>
        <button @click="goBack" class="btn btn-dark mb-4">← 뒤로가기</button>
        <div v-if="isBookMark">
            <div class="container">
                <div v-for="video in videos.items" class="video" @click="goDetail(video)">
                    <img :src="video.snippet.thumbnails.high.url" alt="thumbnail">
                    <p>{{ video.snippet.title }}</p>
                </div>
            </div>
        </div>
        <div v-else class="container">
            <h1>등록된 비디오 없음</h1>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios'
import { computed } from '@vue/reactivity';

const videoIds = ref(JSON.parse(localStorage.getItem('bookMark')))
const videoId = Object.values(videoIds.value).join(",")
const videos = ref('')

const isBookMark = computed(()=>{
    return videoIds.value.length > 0
})

axios({
        url: 'https://www.googleapis.com/youtube/v3/videos',
        method: 'get',
        params: {
          part: 'snippet',
          id : videoId,
          key: 'AIzaSyAuexJ8PlQlJZeeFlyBmfbDCp3-LzXg980'
          }
        })
        .then((response)=>{
          videos.value = response.data
        })
        .catch((error)=>{
          console.log(error)
        })

const router = useRouter()
const goDetail = (video)=>{
  router.push({name:'detail', params:{ pk:video.id }})
}

const goBack = () => {
    router.back()
}
</script>

<style scoped>
.btn {
  margin-left: 30px;
}

</style>