export default{
    initBoard(state, payload){
        let x  = payload.width; let y = payload.height;
        let board = [];
        for (let i = 0; i < y; i ++){
            let row = [];
            for (let j = 0; j < x; j ++){
                if (
                    i >= 0 && i <= 2 && j >= 0 && j <= 2
                ){
                    row.push(2);
                }else{
                    row.push(0);
                }
            }
            board.push(row);
        }

        state.board = board;
        state.updateBoard = true;
    },
    setUuid(state, payload){
        state.uuid = payload;
    },
    setIsPlaying(state, payload){
        state.isPlaying = payload;
    },
    updatePlayers(state, payload){
        Object.keys(payload.players).forEach((key) => {
            if (state.players[key] == null){
                state.players[key] = {
                    positions: {
                        cur: payload.players[key].curPos.slice(),
                        prev: payload.players[key].curPos.slice(),
                    },
                    lineValue: payload.players[key].lineValue,
                    areaValue: payload.players[key].areaValue,
                }
            }else{
                state.players[key].positions.prev = state.players[key].positions.cur.slice();
                state.players[key].positions.cur = payload.players[key].curPos.slice();
            }
        });
    },
    updateBoard(state, payload){
        state.board = payload.board;
        this.commit("setUpdateBoard", true);
    },
    setUpdateBoard(state, payload){
        state.updateBoard = payload;
    },
}