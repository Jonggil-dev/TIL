<template>
    <div class = "detailBox">
      <button @click="goBack" class="btn btn-dark mb-4">← 뒤로가기</button>
      <h2 class="mb-3">{{ title }}</h2>
        <iframe 
        id="player" 
        type="text/html" 
        width="640" 
        height="360"
        :src="`http://www.youtube.com/embed/${videoId}`"
        frameborder="0"></iframe>
     <p class="fw-lighter mt-3">{{ description }}</p>
     <div class = "btnContainer">
        <div v-if="!isVideoDuplicated">
            <button @click="localVideoSave(videoId)" class="btn btn-primary">동영상 저장</button>
        </div>
        <div v-else>
            <button @click="localVideoDelete(videoId)" class="btn btn-primary">동영상 저장 취소</button>
        </div>
        
        <div v-if="!isChannelDuplicated">
            <button @click="localChannelSave(Channel)" class="btn btn-warning">채널 저장</button>
        </div>
        <div v-else>
            <button @click="localChannelDelete(Channel)" class="btn btn-warning">채널 저장 취소</button>
        </div>
    </div>
    </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref,computed } from 'vue'
import axios from 'axios'


//1개 영상 api 요청
const route = useRoute()
const videoId = route.params.pk
const Channel = ref('')
const description = ref('')
const title = ref('')

axios({
    url: 'https://www.googleapis.com/youtube/v3/videos',
    method: 'get',
    params: {
      part: 'snippet',
      id: route.params.pk,
      key: 'AIzaSyAuexJ8PlQlJZeeFlyBmfbDCp3-LzXg980'
    },
  })
    .then(response => {
      const videoDetails = response.data.items[0].snippet;
      Channel.value = videoDetails.channelId
      description.value = videoDetails.description;
      title.value = videoDetails.title;
    })
    .catch(error => {
      console.error('Error fetching video details:', error);
    });


//동영상 저장 구현
const existingVideo = ref(JSON.parse(localStorage.getItem('bookMark')) || [])
const isVideoDuplicated = computed(() => {
    return existingVideo.value.includes(videoId)
})

const localVideoSave = (videoId) =>{
    alert('동영상 저장 목록에 추가합니다')
    existingVideo.value.push(videoId)
    localStorage.setItem('bookMark',JSON.stringify(existingVideo.value))
}
    
const localVideoDelete = (videoId) => {
    alert('동영상 저장 목록에서 취소 되었습니다')


    const itemIdx = existingVideo.value.findIndex((item)=> {return item === videoId})
    existingVideo.value.splice(itemIdx,1)
    localStorage.setItem('bookMark',JSON.stringify(existingVideo.value))
}

//채널 저장 구현
const existingChannel = ref(JSON.parse(localStorage.getItem('Channel')) || [])
const isChannelDuplicated = computed(() => {
    return existingChannel.value.includes(Channel.value)
})

const localChannelSave = (channel) =>{
    alert('채널 저장 목록에 추가합니다')
    existingChannel.value.push(channel)
    localStorage.setItem('Channel',JSON.stringify(existingChannel.value))
}
    
const localChannelDelete = (channel) => {
    alert('채널 저장 목록에서 취소되었습니다')
    const itemIdx = existingChannel.value.findIndex((item)=> {return item === channel})
    existingChannel.value.splice(itemIdx,1)
    localStorage.setItem('Channel',JSON.stringify(existingChannel.value))
}


//뒤로가기 구현
const router = useRouter()
const goBack = () => {
    router.back()
}
</script>

<style scoped>
.detailBox{
    margin-top: 100px;
    margin-left: 50px;
}

.btnContainer{
    display: flex;
    gap: 10px;
}
</style>
