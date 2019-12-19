<template>
  <div>
    <Row type="flex" justify="center" style="padding-top: 72px">
      <img src="@/assets/icon.png" style="border-radius: 999px;background-color: #FFF;padding: 8px">
    </Row>
    <Row>
      <h2 style="font-size: 32px">笔.记</h2>
    </Row>
    <Row>
      <span style="font-size: 18px">Fantastic 2019</span>
    </Row>
    <Row style="margin-top: 16px;" type="flex" justify="center">
      <i-col :span="5">
        <Affix>
          <Menu :active-name="selectMenuItem" style="border-radius: 8px;padding: 12px" @on-select="onItemSelect">
            <MenuItem name="overView">概览</MenuItem>
            <MenuItem name="updates">更新日志</MenuItem>
            <MenuItem name="downloads">资源</MenuItem>
          </Menu>
        </Affix>
      </i-col>
      <i-col :span="16">
        <Card v-if="selectMenuItem === 'overView'">
          <div slot="title">
            <Row type="flex" justify="start">
              <span>工程概览：</span>
            </Row>
          </div>
          <Row type="flex" justify="start" style="padding: 8px;text-align: left">
            <MarkdownItVue class="md-body" :content="overViewText"/>
            <Button style="margin-top: 12px" size="large" @click="gotoGithub('https://github.com/SW-Fantastic/note')"><Icon style="font-size: 36px" type="logo-github" />访问GitHub</Button>
            <Button style="margin-top: 12px;margin-left: 8px" @click="selectMenuItem = 'downloads'" size="large" type="success"><Icon type="ios-cog" style="font-size: 36px" /> 下载此应用</Button>
          </Row>
          <Divider dashed />
          <h2 style="text-align: left;margin-bottom: 8px">运行截图：</h2>
          <Row type="flex" justify="start">
            <viewer :images="screenShots">
              <img
                v-for="(src,index) in screenShots"
                :src="src.src"
                :key="index"
                :style="'width: ' + ((1/screenShots.length) * 100) + '%; border-style:solid;padding:4px;border-width:1px;border-color:#dcdee2'"
              >
            </viewer>
          </Row>
          <span style="text-align : left;font-size: 14px">点击查看大图</span>
        </Card>
        <Card v-else-if="selectMenuItem === 'updates'">
          <div slot="title">
            <Row type="flex" justify="start">
              <span>更新日志：</span>
            </Row>
          </div>
          <Row type="flex" justify="start">
            <Timeline>
              <TimelineItem :key="item.id" v-for="item in commits">
                <div style="text-align: left">
                  <span style="font-size: 16px">{{item.date}}</span>
                  <p>{{item.content}}</p>
                </div>
              </TimelineItem>
            </Timeline>
          </Row>
        </Card>
        <Card v-else-if="selectMenuItem === 'downloads'">
          <div style="text-align: left">
            <h2 style="margin-bottom: 18px">下载应用</h2>
          </div>
          <div style="text-align: left">
            <Card dis-hover :key="key" v-for="(relItem,key) of releases" style="margin-bottom: 6px;">
              <span slot="title">
                <h3>{{relItem.name + '  ' + getDateStr(new Date(relItem.created_at))}}</h3>
              </span>
              <p>{{relItem.body}}</p>
              <Row type="flex" justify="start" align="middle"  style="padding:8px" >
                <Divider dashed />
                <i-col :span="2">
                  <Avatar size="large" :src="relItem.author.avatar_url"></Avatar>
                </i-col>
                <i-col :span="16" style="display: flex;flex-direction: row">
                  <Button  @click="download(item.browser_download_url)" size="large" :key="key" v-for="(item,key) in relItem.assets">
                    下载：{{item.name }}
                  </Button>
                </i-col>
              </Row>
            </Card>
          </div>
        </Card>
        <Row type="flex" justify="center">
          <span style="padding: 8px;font-size:16px">Fantastic MIT License 2019</span>
        </Row>
      </i-col>
    </Row>
  </div>
</template>

<script>
import MarkdownItVue from 'markdown-it-vue'
import 'markdown-it-vue/dist/markdown-it-vue.css'
export default {
  name: 'Home',
  components: { MarkdownItVue },
  data () {
    return {
      selectMenuItem: 'overView',
      overViewText: '',
      screenShots: [ { src: require('@/assets/screenShot0.png') },
        { src: require('@/assets/screenShot1.png') },
        { src: require('@/assets/screenShot2.png') } ],
      commits: [],
      releaseLasted: null,
      releases: []
    }
  },
  created () {
    let dataLocation = require('@/assets/overview.md')
    fetch(dataLocation)
      .then(resp => resp.text())
      .then(resp => {
        this.overViewText = resp
      })
    this.loadCommits()
    this.loadRelease()
    this.loadLastRelease()
  },
  methods: {
    onItemSelect (item) {
      this.selectMenuItem = item
    },
    gotoGithub (url) {
      window.location = url
    },
    loadRelease () {
      fetch('https://api.github.com/repos/SW-Fantastic/note/releases')
        .then(resp => resp.json())
        .then(resp => {
          resp = resp.sort((itemA, itemB) =>
            new Date(itemB.created_at).getTime() - new Date(itemA.created_at).getTime())
          this.releases = []
          let count = resp.length < 5 ? resp.length : 5
          let index
          for (index = 0; index < count; index++) {
            this.releases.push(resp[index])
          }
        })
    },
    loadLastRelease () {
      fetch('https://api.github.com/repos/SW-Fantastic/note/releases/latest')
        .then(resp => resp.json())
        .then(resp => {
          this.releaseLasted = resp
        })
    },
    download (url) {
      window.open(url)
    },
    getDateStr (date) {
      let year = date.getFullYear()
      let month = date.getMonth()
      let day = date.getDate()
      return year + '-' + month + '-' + day
    },
    loadCommits () {
      let date = new Date()
      let year = date.getFullYear()
      let month = date.getMonth()
      let day = 29
      if (month < 4) {
        year = year - 1
        month = 12 - month - 4
      }
      date = new Date(year + '-' + month + '-' + day).toISOString()
      fetch('https://api.github.com/repos/SW-Fantastic/note/commits?since=' + date)
        .then(resp => resp.json())
        .then(resp => {
          let count = resp.length < 10 ? resp.length : 10
          let index
          resp = resp.sort((itemA, itemB) =>
            new Date(itemB.created_at).getTime() - new Date(itemA.created_at).getTime())
          for (index = 0; index < count; index++) {
            let commitItem = resp[index]
            let date = new Date(commitItem.commit.committer.date)
            this.commits.push({
              id: commitItem.node_id,
              avatar: commitItem.committer.avatar_url,
              name: commitItem.committer.login,
              content: commitItem.commit.message,
              date: date.getFullYear() + '-' + date.getMonth() + '-' + date.getDate()
            })
          }
        })
    }
  }
}
</script>

<style scoped>

</style>
