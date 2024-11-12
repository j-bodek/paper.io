export default{
    initBoard(context, payload) {
        context.commit('initBoard', payload);
    },
    updatePlayers(context, payload){
        context.commit('updatePlayers', payload);
    },
    // movePlayer(context){
    //     context.commit('movePlayer');
    // },
    // changeDirection(context, payload){
    //     context.commit('changeDirection', payload);
    // },
    // setUpdateBoard(context, payload){
    //     context.commit('setUpdateBoard', payload);
    // },
    setIsPlaying(context, payload){
        context.commit('setIsPlaying', payload);
    }
}