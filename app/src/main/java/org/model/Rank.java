package org.model;

   public enum Rank {
        ASSO(3,8), DUE(1,9), TRE(1,10), QUATTRO(0,1), CINQUE(0,2), SEI(0,3), SETTE(0,4), FANTE(1,5), CAVALLO(1,6), RE(1,7);

        private final int tressetteValue;
		private final int captureOrder;

        Rank(int tressetteValue,int captureOrder) {
            this.tressetteValue = tressetteValue;
			this.captureOrder = captureOrder;
        }

        public int getTressetteValue() {
            return tressetteValue;
        }

		public int getCaptureOrder(){
			return captureOrder;
		}
    }
