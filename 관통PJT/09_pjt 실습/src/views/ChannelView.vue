<template>
    <div>
        <button @click="goBack" class="btn btn-dark mb-4">← 뒤로가기</button>
        <div v-if="isChannels">
            <div class="container">
                <div v-for="channel in channels.items"  class="video">
                    <a :href="`https://youtube.com/channel/${channel.id}`">
                        <img  :src="channel.snippet.thumbnails.high.url" alt="thumbnail">
                        <p>{{ channel.snippet.title }}</p>
                    </a>
                </div>
            </div>
        </div>
        <div v-else class="container">
            <h1>등록된 채널 없음</h1>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router';
import axios from 'axios'
import { computed } from '@vue/reactivity';

const channelIds = ref(JSON.parse(localStorage.getItem('Channel')))
const channelId = Object.values(channelIds.value).join(",")
const channels = ref('')

const isChannels = computed(()=>{
    return channelIds.value.length > 0
})

axios({
        url: 'https://www.googleapis.com/youtube/v3/channels',
        method: 'get',
        params: {
          part: 'snippet',
          id : channelId,
          key: 'AIzaSyAuexJ8PlQlJZeeFlyBmfbDCp3-LzXg980'
          }
        })
        .then((response)=>{
          channels.value = response.data
          console.log(channels.value)
        })
        .catch((error)=>{
          console.log(error)
        })

const router = useRouter()

const goBack = () => {
    router.back()
}
</script>

<style scoped>
.btn {
  margin-left: 30px;
}

</style>