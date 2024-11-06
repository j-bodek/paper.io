export default{
    isPlaying(state){
        return state.isPlaying;
    },
    getBoard(state){
        return state.board;
    },
    getPlayer(state){
        return state.player;
    },
    getUpdateBoard(state){
        return state.updateBoard;
    },
    getCurPositionValue(state){
        if (state.board === null){
            return null;
        }

        let x = state.player.positions.cur[0];
        let y = state.player.positions.cur[1];

        if (x < 0 || x >= state.board[0].length || y < 0 || y >= state.board.length){
            return null;
        }

        return state.board[y][x];
    }
}