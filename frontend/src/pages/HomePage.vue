
<template>
    <div class="container">
        <div class="wrapper">
            <h1>Paper.io</h1>
            <p>Create room and play with friend!</p>

            <hr>

            <button id="crateRoomBtn">Create Room</button>
        </div>
    </div>
</template>

<script>

export default {
    name: 'HomePage',
    mounted(){
        this.createRoomButton.addEventListener('click', () => {
            this.createRoom();
        });
    },
    computed: {
        createRoomButton(){
            return document.getElementById('crateRoomBtn');
        }
    },
    methods: {
        createRoomUUID() {
            // TODO: replace with api call to create room
            return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, c =>
                (+c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> +c / 4).toString(16)
            );
        },
        createRoom() {
            let uuid = this.createRoomUUID();
            this.$router.push({ name: 'room', params: { uuid: uuid } });
        }
    }
}
</script>

<style scoped>
.container {
    background-color: #e9c46a;
    width: 100%;
    height: 100vh;
    margin: 0 !important;
    padding: 0 !important;
    display: flex;
}

.wrapper {
    margin: auto;
    text-align: center;
}

hr {
    width: 50%;
    margin: 20px auto;
    border: 1px solid black;
}

button {
    background-color: #2a9d8f;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
}

</style>