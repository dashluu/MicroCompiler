package Types;

import Utilities.TokenType;

public abstract class OperatorTypeConversion {
    protected final TokenType opId;

    public OperatorTypeConversion(TokenType opId) {
        this.opId = opId;
    }

    public TokenType getOperatorId() {
        return opId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OperatorTypeConversion opTypeConversion)) {
            return false;
        }
        return opId == opTypeConversion.opId;
    }
}
