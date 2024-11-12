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
                    cur: null,
                    prev: null,
                },
                // line: {start: null, end: null},
                // direction: null,
            },
          }
    },
    mutations:gameMutations,
    actions:gameActions,
    getters:gameGetters,
}