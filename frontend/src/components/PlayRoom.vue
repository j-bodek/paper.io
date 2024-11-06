<template>
  <div class="container">
    <canvas id="board"></canvas>
  </div>
</template>

<script>
export default {
  name: 'PlayRoom',
  mounted() {
    this.runAnimation();
  },
  computed: {
    board() {
      return document.getElementById('board');
    }
  },
  methods: {
    runAnimation() {
      console.log(this.board.width, this.board.height);
      this.$store.dispatch("initBoard", {
        width: this.board.width,
        height: this.board.height
      });
      document.addEventListener('keydown', this.changeDirection);
      let animation = setInterval(() => {
        this.animateBoard(animation);
        if (!this.$store.getters['isPlaying']) {
          clearInterval(animation);
          alert('Game Over');
        }
      }, 20);
    },
    animateBoard() {
      const ctx = this.board.getContext('2d');
      // ctx.clearRect(0, 0, this.board.width, this.board.height);
      let player = this.$store.getters['getPlayer'];
      ctx.fillStyle = 'green';

      if (this.$store.getters['getUpdateBoard']) {
        console.log("update board");
        let board = this.$store.getters['getBoard'];
        for (let i = 0; i < board.length; i++) {
          for (let j = 0; j < board[i].length; j++) {
            if (board[i][j] === 2) {
              ctx.fillRect(j, i, 3, 2);
            }
          }
        }
        this.$store.dispatch('setUpdateBoard', false);
      }

      ctx.fillRect(player.positions.cur[0], player.positions.cur[1], 3, 2);
      this.$store.dispatch('movePlayer');
    },
    changeDirection(e) {
      // listed four keys: w, a, s, d
      // console.log(e.keyCode);
      if (e.keyCode === 87) {
        // move up
        this.$store.dispatch('changeDirection', { direction: [0, -1] });
      } else if (e.keyCode === 65) {
        // move left
        this.$store.dispatch('changeDirection', { direction: [-1, 0] });
      } else if (e.keyCode === 83) {
        // move down
        this.$store.dispatch('changeDirection', { direction: [0, 1] });
      } else if (e.keyCode === 68) {
        // move right
        this.$store.dispatch('changeDirection', { direction: [1, 0] });
      }
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.container {
  display: flex;
  width: 100%;
  height: 100vh;
  margin: 0;
  align-items: center;
  justify-content: center;
}

#board {
  width: 1000px;
  height: 700px;
  background: blue;
}
</style>
