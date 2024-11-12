export default{
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