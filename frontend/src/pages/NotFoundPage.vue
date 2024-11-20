
<template>
    <div class="container">
        <div class="wrapper">
            <h1>Not Found Page</h1>

            <hr>

            <button id="backToHomeBtn">Back To Home</button>
        </div>
    </div>
</template>

<script>

export default {
    name: 'NotFoundPage',
    mounted(){
        this.backToHomeButton.addEventListener('click', () => {
            this.backToHome();
        });
    },
    computed: {
        backToHomeButton(){
            return document.getElementById('backToHomeBtn');
        }
    },
    methods: {
        backToHome() {
            this.$router.push({ name: 'home' });
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