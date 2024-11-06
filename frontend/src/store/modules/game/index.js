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
            // player coordinates
            player: {
                positions: {
                    cur: [40, 40],
                    prev: [40, 40],
                },
                line: {start: null, end: null},
                direction: [1, 0],
            },
          }
    },
    mutations:gameMutations,
    actions:gameActions,
    getters:gameGetters,
}