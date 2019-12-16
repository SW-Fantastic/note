import Vue from 'vue'
import App from './App.vue'
import router from './router'

import ViewUI from 'iview'
import 'iview/dist/styles/iview.css'

import Viewer from 'v-viewer'
import 'viewerjs/dist/viewer.css'

Viewer.setDefaults({
  Options: { 'inline': true, 'button': true, 'navbar': true, 'title': true, 'toolbar': true, 'tooltip': true, 'movable': true, 'zoomable': true, 'rotatable': true, 'scalable': true, 'transition': true, 'fullscreen': true, 'keyboard': true, 'url': 'data-source' }
})

Vue.config.productionTip = false

Vue.use(ViewUI)
Vue.use(Viewer)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
