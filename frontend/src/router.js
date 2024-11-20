import { createRouter, createWebHistory } from 'vue-router';

// pages
import HomePage from './pages/HomePage.vue';
import RoomPage from './pages/RoomPage.vue';
import NotFoundPage from './pages/NotFoundPage.vue';

const router = createRouter({
    base: process.env.VUE_APP_BASE_URL,
    history: createWebHistory(),
    routes:[
        {path: '/', name:"home", component: HomePage},
        {path: '/room/:uuid([a-f0-9]{8}-?[a-f0-9]{4}-?4[a-f0-9]{3}-?[89ab][a-f0-9]{3}-?[a-f0-9]{12}|[a-f0-9]{8}-?[a-f0-9]{4}-?4[a-f0-9]{3}-?[89ab][a-f0-9]{3}-?[a-f0-9]{12})/', name:"room", component: RoomPage},
        {path:"/:notFound(.*)*", name:"not_found", component: NotFoundPage}
    ]
});

// router.beforeEach((to, from, next) => {
//     function checkRoomExists(uuid){
//         // todo: implement check logic
//         return true;
//     }

//     if (to.name === 'room'){
//         let uuid = to.params.uuid;
//         if(!checkRoomExists(uuid)) {
//             next("/not-found");
//         } else {
//             next();
//         }
//     }
// });

export default router;