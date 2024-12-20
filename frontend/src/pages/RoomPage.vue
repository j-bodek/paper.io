<template>
    <!-- Play wrapper -->
    <div class="play-container" :class="{'hidden': started}">
        <div v-if="verdict" class="verdict">
          <h2>{{ verdict }}</h2>
        </div>
  
        <h1>Press play to start a game</h1>
        <p class="instructions">Use W, A, S, D to move</p>
  
        <button id="playButton" class="play-button">Play</button>
    </div>
  
  
    <div class="timer">
      <p v-if="$store.getters['isPlaying']">Time: {{ Math.floor(this.gameplayTime / 1000) }}</p>
      <p v-else>Waiting for player to join</p>
    </div>
    <div class="container">
      <div class="canvas-container">
        <div class="canvas-wrapper">
          <canvas id="background" width="2000px" height="2000px"></canvas>
          <canvas id="player-1" width="2000px" height="2000px"></canvas>
          <canvas id="player-2" width="2000px" height="2000px"></canvas>
          <canvas id="grid" width="2000px" height="2000px"></canvas>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  
  import { Client } from '@stomp/stompjs';
  
  export default {
    name: 'RoomPage',
    data() {
      return {
        started: false,
        verdict: null,
        gameplayTime: 0,
        stompClient: null
      }
    },
    mounted() {
      this.listenGameStarted();
    },
    computed: {
      uuid() {
        return this.$store.getters['getUuid'];
      },
      squareSize() {
        return this.board.width / 50;
      },
      gridBoard() {
        return document.getElementById('grid');
      },
      board() {
        return document.getElementById('background');
      },
      canvasContainer() {
        return document.querySelector('.canvas-container');
      },
      container() {
        return document.querySelector('.container');
      },
      playButton() {
        return document.getElementById('playButton');
      }
    },
    methods: {
      listenGameStarted() {
        this.playButton.addEventListener('click', () => {
          this.startGame();
        });
      },
      initClient() {
  
        this.stompClient = new Client({
          brokerURL: 'ws://localhost:8000/ws-server'
        });
  
        this.stompClient.onConnect = () => {
          this.joinRoom();
  
          this.stompClient.subscribe('/room/subscribe', (message) => {
            this.gameListener(JSON.parse(message.body));
          });
        };
  
        this.stompClient.onWebSocketError = (error) => {
          console.error('Error with websocket', error);
        };
  
        this.stompClient.onStompError = (frame) => {
          console.error('Broker reported error: ' + frame.headers['message']);
          console.error('Additional details: ' + frame.body);
        };
  
        this.stompClient.activate();
      },
      joinRoom() {
        // generate uuid
        this.stompClient.publish({ destination: '/app/room/join', body: JSON.stringify({ "id": this.uuid, "name": "test" }) });
      },
      gameListener(data) {
        if (data.type === 'players') {
          this.gameplayTime = data.gameplayTime;
          let currentPlayersLen = Object.keys(this.$store.getters['getPlayers']).length;
          this.$store.dispatch('updatePlayers', { players: data.players });
  
          if (currentPlayersLen !== Object.keys(data.players).length) {
            Object.keys(data.players).forEach(key => {
              this.animateBoard(key);
            });
          }
  
          this.animatePlayers();
        } else if (data.type === 'board') {
          // update board
          this.$store.dispatch('updateBoard', { board: data.board });
          this.animateBoard(data.playerId);
        } else if (data.type === 'gameStarted') {
          this.$store.dispatch('setIsPlaying', true);
        } else if (data.type === 'gameOver') {
          this.endGame(data);
        }
      },
      endGame(data) {
        setTimeout(() => {
          this.$store.dispatch('setIsPlaying', false);
          if (data.winnerId === null) {
            this.verdict = "It's a draw!";
          } else if (data.winnerId === this.uuid) {
            this.verdict = "You won!";
          } else {
            this.verdict = "You lost!";
          }
  
          document.removeEventListener('keydown', this.changeDirection);
          this.stompClient.deactivate();
          this.stompClient = null;
          this.gameplayTime = 0;
          this.$store.dispatch('reset');
          this.clearCanvas();
          this.started = false;
        }, 50);
      },
      clearCanvas() {
        let ctxBg = this.board.getContext('2d');
        let ctxGrid = this.gridBoard.getContext('2d');
        let ctxPlayer1 = this.getPlayerBoard('player-1').getContext('2d');
        let ctxPlayer2 = this.getPlayerBoard('player-2').getContext('2d');
  
        let canvases = {
          ctxBg, ctxGrid, ctxPlayer1, ctxPlayer2
        };
  
        Object.keys(canvases).forEach(key => {
          canvases[key].clearRect(0, 0, this.board.width, this.board.height);
        });
      },
      startGame() {
        this.started = true;
        this.verdict = null;
  
        let uuid = Math.random().toString(36).substring(7);
        this.$store.dispatch('setUuid', uuid);
  
        this.$store.dispatch("initBoard", {
          width: 50,
          height: Math.floor(this.board.height / this.squareSize),
        });
        document.addEventListener('keydown', this.changeDirection);
        this.drawGrid();
  
        this.initClient();
      },
      drawGrid() {
        let bw = this.gridBoard.width;
        // Box height
        let bh = this.gridBoard.height;
        // Padding
        let p = 0;
  
        let canvas = this.gridBoard;
        let context = canvas.getContext("2d");
  
  
        for (let x = this.squareSize; x <= bw; x += this.squareSize) {
          context.moveTo(x + p, p);
          context.lineTo(x + p, bh + p);
        }
  
        for (let x = this.squareSize; x <= bh; x += this.squareSize) {
          context.moveTo(p, x + p);
          context.lineTo(bw + p, x + p);
        }
        context.strokeStyle = "black";
        context.stroke();
      },
      animatePlayers() {
        let players = this.$store.getters['getPlayers'];
        let colorsMap = this.$store.getters['getColorsMap'];
  
        let board = this.$store.getters['getBoard'];
        Object.keys(players).forEach((key) => {
          let ctx = this.getPlayerBoard(key).getContext('2d');
          let player = players[key];
          ctx.fillStyle = colorsMap[player.lineValue];
  
          // if user is within area clean line
          if (board[player.positions.prev[1]][player.positions.prev[0]] === player.areaValue) {
            ctx.clearRect(0, 0, this.board.width, this.board.height);
          }
  
          ctx.fillRect(
            player.positions.cur[0] * this.squareSize,
            player.positions.cur[1] * this.squareSize,
            this.squareSize, this.squareSize
          );
  
          // update only current player
          if (key === this.uuid) {
            this.updateCamera(player.positions.cur);
          }
        });
      },
      animateBoard(playerId) {
        const ctxBg = this.board.getContext('2d');
        const ctx = this.getPlayerBoard(playerId).getContext('2d');
  
        let players = this.$store.getters['getPlayers'];
        let colorMap = this.$store.getters['getColorsMap'];
  
        ctx.clearRect(0, 0, this.board.width, this.board.height);
  
        let board = this.$store.getters['getBoard'];
  
        for (let i = 0; i < board.length; i++) {
          for (let j = 0; j < board[i].length; j++) {
            if (board[i][j] !== 0 && board[i][j] % 2 === 0) {
              // area
              ctxBg.fillStyle = colorMap[board[i][j]];
              ctxBg.fillRect(j * this.squareSize, i * this.squareSize, this.squareSize, this.squareSize);
            } else if (board[i][j] === players[playerId].lineValue) {
              // line
              ctx.fillStyle = colorMap[board[i][j]];
              ctx.fillRect(j * this.squareSize, i * this.squareSize, this.squareSize, this.squareSize);
            }
          }
        }
  
      },
      updateCamera(currentPosition) {
        let padding = 100;
        if (currentPosition[0] * this.squareSize + padding >= this.container.clientWidth / 2) {
          let scrollLeft = Math.min(
            currentPosition[0] * this.squareSize + padding - this.container.clientWidth / 2,
            this.$store.getters['getBoard'].length * this.squareSize + 2 * padding - this.container.clientWidth
          )
          this.canvasContainer.style.left = `-${scrollLeft}px`;
        } else {
          this.canvasContainer.style.left = `0px`;
        }
  
        if (currentPosition[1] * this.squareSize + padding >= this.container.clientHeight / 2) {
          let scrollTop = Math.min(
            currentPosition[1] * this.squareSize + padding - this.container.clientHeight / 2,
            this.$store.getters['getBoard'][0].length * this.squareSize + 2 * padding - this.container.clientHeight
          )
          this.canvasContainer.style.top = `-${scrollTop}px`;
        } else {
          this.canvasContainer.style.top = `0px`;
        }
      },
      getPlayerBoard(playerId) {
        if (playerId === this.uuid) {
          return document.getElementById('player-1');
        } else {
          return document.getElementById('player-2');
        }
      },
      changeDirection(e) {
        // listed four keys: w, a, s, d
        if (e.keyCode === 87) {
          // move up
          this.stompClient.publish({ destination: '/app/room/move', body: JSON.stringify({ "id": this.uuid, "roomId": "room", "direction": [0, -1] }) });
        } else if (e.keyCode === 65) {
          // move left
          this.stompClient.publish({ destination: '/app/room/move', body: JSON.stringify({ "id": this.uuid, "roomId": "room", "direction": [-1, 0] }) });
        } else if (e.keyCode === 83) {
          // move down
          this.stompClient.publish({ destination: '/app/room/move', body: JSON.stringify({ "id": this.uuid, "roomId": "room", "direction": [0, 1] }) });
        } else if (e.keyCode === 68) {
          // move right
          this.stompClient.publish({ destination: '/app/room/move', body: JSON.stringify({ "id": this.uuid, "roomId": "room", "direction": [1, 0] }) });
        }
      }
    }
  }
  </script>
  
  <!-- Add "scoped" attribute to limit CSS to this component only -->
  <style scoped>
  
  .play-container {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    background: #e9c46a;
    z-index: 200;
  }
  
  .play-container.hidden {
    display: none;
  }
  
  .play-container button{
    padding: 10px 20px;
    background: #2a9d8f;
    color: white;
    border: none;
    cursor: pointer;
    font-weight: bold;
    transition: all 0.15s;
  }
  
  .play-container button:hover {
    background: #264653;
  }
  
  .play-container h1 {
    margin: 10px 0;
  }
  
  .play-container .instructions {
    margin: 10px 0;
  }
  
  .timer {
    position: absolute;
    top: 0;
    right: 0;
    left: 0;
    padding: 10px;
    margin: 10px;
    background: white;
    border: 1px solid black;
    color: black;
    font-weight: bold;
    z-index: 100;
  }
  
  .timer p {
    margin: 0;
    padding: 0;
  }
  
  .container {
    position: absolute;
    width: 100%;
    height: 100vh;
    margin: 0;
    background: gray;
    overflow: hidden;
  }
  
  .canvas-container {
    position: absolute;
    padding: 100px;
    top: 0;
    left: 0;
  }
  
  .canvas-wrapper {
    position: absolute;
    padding: 0;
    width: 2000px;
    height: 2000px;
    border: 2px solid black;
  }
  
  #background {
    top: 0;
    left: 0;
    position: absolute;
    width: 2000px;
    height: 2000px;
    background: blue;
  }
  
  #grid {
    top: 0;
    left: 0;
    position: absolute;
    width: 2000px;
    height: 2000px;
    background: transparent;
  }
  
  #player-1,
  #player-2 {
    top: 0;
    left: 0;
    position: absolute;
    width: 2000px;
    height: 2000px;
    background: transparent;
  }
  </style>
  