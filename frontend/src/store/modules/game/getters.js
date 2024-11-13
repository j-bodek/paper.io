export default{
    getUuid(state){
        return state.uuid;
    },
    getColorsMap(state){
        let colors = {};

        Object.keys(state.players).forEach((key) => {
            if (key == state.uuid){
                colors[state.players[key].lineValue] = state.colors.playerLine;
                colors[state.players[key].areaValue] = state.colors.playerArea;
            }else{
                colors[state.players[key].lineValue] = state.colors.opponentLine;
                colors[state.players[key].areaValue] = state.colors.opponentArea;
            }
        });

        return colors;
    },
    isPlaying(state){
        return state.isPlaying;
    },
    getBoard(state){
        return state.board;
    },
    getPlayers(state){
        return state.players;
    },
    getUpdateBoard(state){
        return state.updateBoard;
    },
}