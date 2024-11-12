import gameMutations from './mutations.js';
import gameActions from './actions.js';
import gameGetters from './getters.js';

export default{
    state(){
        return{
            // general game state
            isPlaying: true,
            updateBoard: false,
            board: null,
            // players state
            players: {},
            // players: {
            //     null: {
            //         positions: {
            //             cur: null,
            //             prev: null,
            //         },
            //         lineValue: null,
            //         areaValue: null,
            //     }
            // },
          }
    },
    mutations:gameMutations,
    actions:gameActions,
    getters:gameGetters,
}