<template>
    <div class container>
      <div class="container">
            <div v-for="video in videos.items" class="video" @click="goDetail(video)">
              <img :src="video.snippet.thumbnails.high.url" alt="thumbnail">
              <p>{{ video.snippet.title }}</p>
            </div>
      </div>
    </div>
</template>

<script setup>
import axios from 'axios'
import { ref } from 'vue'
import { useRouter } from 'vue-router';
const youtubeUrl = 'https://www.googleapis.com/youtube/v3/videos'
const videos = ref('')

axios({
        url: youtubeUrl,
        method: 'get',
        params: {
          part: 'snippet',
          chart: 'mostPopular',
          regionCode : 'KR',
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
  

const router = useRouter()
const goDetail = (video)=>{
router.push({name:'detail', params:{ pk:video.id }})
}

</script>

<style>
.container {
  display: flex;
  /* width: 200px; */
  margin-top: 30px;
  gap: 10px;
  flex-wrap: wrap;
}

.video{
  border: 2px solid black;
}
.video img {
  width: 200px; /* 이미지 너비를 줄입니다. */
  height: auto; /* 높이를 auto로 설정하여 비율을 유지합니다. */
  cursor: pointer; 
  transition: transform 0.3s ease;
}

.video p {
  font-size: 14px; /* 텍스트 크기를 줄입니다. */
  cursor: pointer;
  width: 200px;
  padding: 10px;
  text-align: center;
}

.video img:hover,
.video p:hover {
  transform: scale(1.05);
}
</style>
