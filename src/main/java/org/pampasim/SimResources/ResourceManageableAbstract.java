package org.pampasim.SimResources;

public class ResourceManageableAbstract implements ResourceManageable {
    private long availableResource;
    private final String unit;
    protected long capacity;

    public ResourceManageableAbstract(final long capacity, final String unit) {
        this.capacity = capacity; //todo: check for negative values
        this.unit = unit; //todo: check for blank or null entries.
    }
    @Override
    public boolean isAmountAvailable(double amountToCheck) {
        return getAvailableResource() >= amountToCheck;
    }
    @Override
    public long getAvailableResource() {
        return availableResource;
    }

    @Override
    public long getAllocatedResource() {
        return getCapacity() - getAvailableResource();
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public boolean setCapacity(final long newcapacity) {
        if(newcapacity < 0 || getAllocatedResource() > newcapacity){
            return false;
        }
        final long oldCapacity = getCapacity();
        this.capacity = newcapacity;
        return true;
    }

    @Override
    public boolean sumCapacity(long amountToSum) {
        if(amountToSum < 0) {
            return false;
        }
        return addCapacity(amountToSum);
    }

    @Override
    public boolean addCapacity(final long capacityToAdd) {
        return setCapacity(getCapacity() + capacityToAdd);
    }

    @Override
    public boolean removeCapacity(final long capacityToRemove) {
        if(capacityToRemove > this.getCapacity()) {
            throw new IllegalStateException(); //todo: add better error message
        }
        return setCapacity(getCapacity() - capacityToRemove);
    }

    @Override
    public boolean allocateResource(final long amountToAllocate) {
        if(amountToAllocate <= 0 || !isAmountAvailable(amountToAllocate)) {
            return false;
        }
        this.availableResource = getAvailableResource() - amountToAllocate;
        return true;
    }

    @Override
    public boolean setAllocatedResource(long newTotalAllocatedResource) {
        if(newTotalAllocatedResource < 0 || !isSuitable(newTotalAllocatedResource)) {
            return false;
        }
        deallocateAllResources();
        return allocateResource(newTotalAllocatedResource);
    }
    @Override
    public boolean deallocateResource(long amountToDeallocate) {
        if(amountToDeallocate <= 0 || !isResourceAmountBeingUsed(amountToDeallocate)) {
            return false;
        }
        this.availableResource = getAvailableResource() + amountToDeallocate;
        return true;
    }
    @Override
    public boolean deallocateAndRemoveResource(final long amountToDeallocate) {
        if(!deallocateResource(getActualAmountToDeallocate(amountToDeallocate))){
            return false;
        }
        return removeCapacity(amountToDeallocate);
    }
    /**
     * Gets the actual amount of resource that can be deallocated.
     * If the amount requested to deallocate is greater than the allocated one,
     * returns just the allocated amount. Otherwise, return the exact requested amount.
     * @param amountToDeallocate the amount requested to deallocate
     * @return the actual amount do deallocate
     */
    private long getActualAmountToDeallocate(final long amountToDeallocate) {
        return Math.min(amountToDeallocate, this.getAllocatedResource());
    }
    @Override
    public long deallocateAllResources() {
        final long previousAllocated = getAllocatedResource();
        this.availableResource = getCapacity();
        return previousAllocated;
    }
    @Override
    public boolean isResourceAmountBeingUsed(long amountToCheck) {
        return getAvailableResource() >= amountToCheck;
    }
    @Override
    public boolean isSuitable(long newTotalAllocatedResource) {
        if(newTotalAllocatedResource <= getAllocatedResource()){
            return true;
        }
        final long allocationDifference = newTotalAllocatedResource - getAllocatedResource();
        return isAmountAvailable(allocationDifference);
    }
    @Override
    public String getUnit() {
        return "";
    }
}