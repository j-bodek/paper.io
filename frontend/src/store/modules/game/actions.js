export default{
    initBoard(context, payload) {
        context.commit('initBoard', payload);
    },
    updatePlayers(context, payload){
        context.commit('updatePlayers', payload);
    },
    updateBoard(context, payload){
        context.commit('updateBoard', payload);
    },
    setIsPlaying(context, payload){
        context.commit('setIsPlaying', payload);
    }
}