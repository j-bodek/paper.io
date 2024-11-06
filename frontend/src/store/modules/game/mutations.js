function checkEnclosedPoint(board, payload){

    let directions = [[1, 0], [-1, 0], [0, 1], [0, -1]];
    for (let i = 0; i < directions.length; i++){
        let cur = payload.cur;
        let x = cur[0] + directions[i][0];
        let y = cur[1] + directions[i][1];

        let foundBorder = false;
        while (
            x >= 0 && x < board[0].length && y > 0 && y < board.length
        ){
            if (payload.boarders.includes(board[y][x])){
                foundBorder = true;
                break;
            }

            x += directions[i][0];
            y += directions[i][1];
        }
        
        if (!foundBorder){
            return false;
        }
    }

    return true;
}

function findEnclosedPoint(board, payload){
    let x = payload.x; let y = payload.y;
    let enclosedPoint = null;
    let queue = [[x, y]];
    let visited = new Set([`${x},${y}`]);
    let directions = [[1, 0], [-1, 0], [0, 1], [0, -1]];

    while (queue.length > 0){

        let cur = queue.shift();
        let x = cur[0]; let y = cur[1];

        if (
            // if point not in board
            x < 0 || x >= board[0].length || y < 0 || y >= board.length
        ){
            continue;
        }

        if (
            board[y][x] === 0
            && checkEnclosedPoint(board, {cur: cur, boarders: payload.boarders})
        ){
            enclosedPoint = cur;
            break;
        }

        for (let i = 0; i < directions.length; i ++){
            let cell = `${x + directions[i][0]},${y + directions[i][1]}`;
            if (visited.has(cell)){
                continue;
            }

            queue.push([x + directions[i][0], y + directions[i][1]]);
            visited.add(cell);
        }
    }

    return enclosedPoint
}


export default{
    initBoard(state, payload){
        let x  = payload.width; let y = payload.height;
        let board = [];
        for (let i = 0; i < y; i ++){
            let row = [];
            for (let j = 0; j < x; j ++){
                if (
                    i >= 20 && i <= 40 && j >= 20 && j <= 40
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
    movePlayer(state){
        state.player.positions.prev = state.player.positions.cur.slice();
        state.player.positions.cur[0] += state.player.direction[0];
        state.player.positions.cur[1] += state.player.direction[1];

        // detect collision
        if (
            state.player.positions.cur[0] < 0 
            || state.player.positions.cur[0] >= state.board[0].length 
            || state.player.positions.cur[1] < 0 
            || state.player.positions.cur[1] >= state.board.length
            || state.board[state.player.positions.cur[1]][state.player.positions.cur[0]] === 1
        ){
            console.log("Player collision detected");
            state.isPlaying = false;
            return;
        }

        this.commit("updateLines", {positions: state.player.positions});
        this.commit("updateBoard", {x: state.player.positions.cur[0], y: state.player.positions.cur[1], value: 1});
    },
    changeDirection(state, payload){
        state.player.direction = payload.direction;
    },
    updateLines(state, payload){
        if (
            payload.positions.cur[0] < 0 
            || payload.positions.cur[0] >= state.board[0].length 
            || payload.positions.cur[1] < 0 
            || payload.positions.cur[1] >= state.board.length
        ){
            return;
        }

        if (
            state.board[payload.positions.prev[1]][payload.positions.prev[0]] === 2
            && state.board[payload.positions.cur[1]][payload.positions.cur[0]] !== 2
        ){
            state.player.line.start = payload.positions.cur.slice();
        }
        
        if (
            state.board[payload.positions.prev[1]][payload.positions.prev[0]] !== 2
            && state.board[payload.positions.cur[1]][payload.positions.cur[0]] === 2
        ){
            state.player.line.end = payload.positions.prev.slice();
        }
    },
    updateBoard(state, payload){
        if (
            payload.x > 0 
            && payload.x < state.board[0].length 
            && payload.y > 0 
            && payload.y < state.board.length
            && state.board[payload.y][payload.x] !== 2
        ){
            state.board[payload.y][payload.x] = payload.value;
        }

        // if line start and end are set, fill area between them
        if (state.player.line.start != null && state.player.line.end != null){
            let point = findEnclosedPoint(state.board, {x: state.player.positions.cur[0], y: state.player.positions.cur[1], boarders: [1, 2]});
            if (point != null){
                this.commit("fillArea", {start: point, boarders: [1, 2]});
                state.updateBoard = true;
            }

            state.player.line.start = null;
            state.player.line.end = null;
        }
    },
    setUpdateBoard(state, payload){
        state.updateBoard = payload;
    },
    fillArea(state, payload){
        let stack = [payload.start];
        let visited = new Set([`${payload.start[0]},${payload.start[1]}`]);

        let directions = [[1, 0], [-1, 0], [0, 1], [0, -1]];

        while (stack.length > 0){
            let cur = stack.pop();
            let x = cur[0]; let y = cur[1];

            if (
                x < 0 || x >= state.board[0].length || y < 0 || y >= state.board.length
            ){
                continue;
            }

            // check if cell is already filled or is a border
            if (payload.boarders.includes(state.board[y][x])){
                state.board[y][x] = 2;
                continue;
            }

            state.board[y][x] = 2;

            for (let i = 0; i < directions.length; i ++){
                let cell = `${x + directions[i][0]},${y + directions[i][1]}`;
                if (visited.has(cell)){
                    continue;
                }

                stack.push([x + directions[i][0], y + directions[i][1]]);
                visited.add(cell);
            }
        }
    }
}