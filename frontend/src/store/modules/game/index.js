import gameMutations from './mutations.js';
import gameActions from './actions.js';
import gameGetters from './getters.js';

export default{
    state(){
        return{
            // general game state
            uuid: null,
            isPlaying: true,
            updateBoard: false,
            board: null,
            colors: {
                playerLine: "#4ED0C1",
                playerArea: "#2A9D8F",
                opponentLine: "#F8C8A0",
                opponentArea: "#F4A261",
            },
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