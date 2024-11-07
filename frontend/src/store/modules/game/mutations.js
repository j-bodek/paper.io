function findBoudingBox(board, payload){
    let x = payload.x; let y = payload.y;
    let maxX = x; let maxY = y; let minX = x; let minY = y;
    let queue = [[x, y]];
    let visited = new Set([`${x},${y}`]);
    let directions = [[1, 0], [-1, 0], [0, 1], [0, -1]];

    while (queue.length > 0){

        let cur = queue.shift();
        let x = cur[0]; let y = cur[1];

        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);

        for (let i = 0; i < directions.length; i ++){
            let nextX = x + directions[i][0]; let nextY = y + directions[i][1];
            let cell = `${nextX},${nextY}`;

            if (
                // if point visited, not in board or has value different from line
                visited.has(cell)
                || nextX < 0 || nextX >= board[0].length || nextY < 0 || nextY >= board.length
                || !payload.values.includes(board[nextY][nextX])
            ){
                continue;
            }

            queue.push([nextX, nextY]);
            visited.add(cell);
        }
    }

    return [minX, minY, maxX, maxY];
}


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
    setIsPlaying(state, payload){
        state.isPlaying = payload;
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
            payload.x >= 0 
            && payload.x < state.board[0].length 
            && payload.y >= 0 
            && payload.y < state.board.length
            && state.board[payload.y][payload.x] !== 2
        ){
            state.board[payload.y][payload.x] = payload.value;
        }

        // if line start and end are set, fill area between them
        if (state.player.line.start != null && state.player.line.end != null){
            let boundingBox = findBoudingBox(state.board, {x: state.player.positions.cur[0], y: state.player.positions.cur[1], values: [1, 2]});
            this.commit("fillBoundingBox", {boundingBox: boundingBox});
            this.commit("fillNewArea", {value: 2, boundingBox: boundingBox});
            state.updateBoard = true;

            state.player.line.start = null;
            state.player.line.end = null;
        }
    },
    setUpdateBoard(state, payload){
        state.updateBoard = payload;
    },
    fillNewArea(state, payload){
        let [minX, minY, maxX, maxY] = payload.boundingBox;

        for (let col = minY; col <= maxY; col ++){
            for (let row = minX; row <= maxX; row ++){
                if (state.board[col][row] === -1){
                    state.board[col][row] = 0;
                }else{
                    state.board[col][row] = payload.value;
                }
            }
        }

    },
    fillBoundingBox(state, payload){
        let [minX, minY, maxX, maxY] = payload.boundingBox;

        for (let col = minY; col <= maxY; col ++){
            if (col === minY || col === maxY){
                for (let row = minX; row <= maxX; row ++){
                    this.commit("fillArea", {start: [row, col], value: 0, boundingBox: payload.boundingBox});
                }
            }else{
                this.commit("fillArea", {start: [minX, col], value: 0, boundingBox: payload.boundingBox});
                this.commit("fillArea", {start: [maxX, col], value: 0, boundingBox: payload.boundingBox});
            }
        }

    },
    fillArea(state, payload){
        if (state.board[payload.start[1]][payload.start[0]] !== payload.value){
            return;
        }

        let stack = [payload.start];
        let [minX, minY, maxX, maxY] = payload.boundingBox;
        let visited = new Set([`${payload.start[0]},${payload.start[1]}`]);

        let directions = [[1, 0], [-1, 0], [0, 1], [0, -1]];

        while (stack.length > 0){
            let cur = stack.pop();
            let x = cur[0]; let y = cur[1];

            
            for (let i = 0; i < directions.length; i ++){
                let nextX = x + directions[i][0];
                let nextY = y + directions[i][1];

                let cell = `${nextX},${nextY}`;

                if (visited.has(cell)){
                    continue;
                }

                if (
                    nextX < minX || nextX > maxX || nextY < minY || nextY > maxY
                ){
                    continue;
                }

                if (state.board[nextY][nextX] === payload.value){
                    stack.push([nextX, nextY]);
                    visited.add(cell);
                }

            }

            state.board[y][x] = -1;
        }
    }
}