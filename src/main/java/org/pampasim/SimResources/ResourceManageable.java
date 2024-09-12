package org.pampasim.SimResources;

public interface ResourceManageable {

    default boolean isSubClassOf(final Class queriedClass) {
        return queriedClass.isAssignableFrom(this.getClass());
    }
    default double getPercentUtilization() {
        return getCapacity() > 0 ? getAllocatedResource() / (double) getAvailableResource() : 0.0;
    }
    default boolean isFull() {return getAvailableResource() <= 0;}

    boolean isAmountAvailable(double amountToCheck);
    long getAvailableResource();
    long getAllocatedResource();
    long getCapacity();
    boolean setCapacity(long capacity);
    boolean sumCapacity(long amountToSum);
    boolean addCapacity(long amountToAdd);
    boolean removeCapacity(long amountToRemove);
    boolean allocateResource(long amountToAllocate);
    boolean setAllocatedResource(long newTotalAllocatedResource);
    boolean deallocateResource(long amountToDeallocate);
    boolean deallocateAndRemoveResource(long amountToDeallocate);
    long deallocateAllResources();
    boolean isResourceAmountBeingUsed(long amountToCheck);
    boolean isSuitable(long newTotalAllocatedResource);
    String getUnit();
}
