import {createStore} from 'vuex';

// import modules
import GameModule from './modules/game/index.js';

const store = createStore({
    modules:{
        gameModule: GameModule,
    }
});

export default store;