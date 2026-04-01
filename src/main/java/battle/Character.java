    package battle;

    import java.util.Random;

    public class Character {

        private int damageOutput;
        private int heartBeat;
        private int maxHeartBeat = 200;
        private double resistance;

        private Random random = new Random();

        public Character(int heartBeat, double resistance) {
            this.heartBeat = heartBeat;
            this.resistance = resistance;
        }

        public int skill1() {
            this.damageOutput = random.nextInt(11) + 10;
            return this.damageOutput;
        }

        public int skill2() {
            this.damageOutput = random.nextInt(11) + 20;
            return this.damageOutput;
        }

        public int skill3() {
            this.damageOutput = random.nextInt(30) + 45;
            return this.damageOutput;
        }

        public void defend() {
            this.resistance = random.nextInt(31) + 30;
        }

        public int getHeartBeat() {
            return heartBeat;
        }

        public void setHeartBeat(int heartBeat) {
            this.heartBeat = heartBeat;
        }

        public int getMaxHeartBeat() {
            return maxHeartBeat;
        }

        public void setMaxHeartBeat(int maxHeartBeat) {
            this.maxHeartBeat = maxHeartBeat;
        }

        public void setIsAlive(boolean isAlive) {
        }

        public double getResistance() {
            return resistance;
        }

        public int getDamageOutput() {
            return damageOutput;
        }

        public boolean getIsAlive() {
            return heartBeat >= 40 && heartBeat <= maxHeartBeat;
        }
    }
