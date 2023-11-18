package requious.recipe;

import requious.data.component.ComponentBase;

public abstract class ConsumptionResult<T> {
    RequirementBase requirement;
    ComponentBase.Slot slot;
    T consumed;

    public ConsumptionResult(RequirementBase requirement, T consumed) {
        this.requirement = requirement;
        this.consumed = consumed;
    }

    public T getConsumed() {
        return consumed;
    }

    public abstract void add(T amount);

    public void setSlot(ComponentBase.Slot slot) {
        this.slot = slot;
    }

    public void consume() {
        requirement.consume(slot, this);
    }

    public static class Long extends ConsumptionResult<java.lang.Long> {
        public Long(RequirementBase requirement, java.lang.Long consumed) {
            super(requirement, consumed);
        }

        @Override
        public void add(java.lang.Long amount) {
            consumed += amount;
        }
    }
}
