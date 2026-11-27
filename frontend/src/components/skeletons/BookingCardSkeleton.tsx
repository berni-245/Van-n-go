import { Component } from "solid-js";

const BookingCardSkeleton: Component<{ class?: string }> = (props) => {
  return (
    <article
      class={`max-w-sm overflow-hidden rounded-radius border border-outline bg-surface-alt shadow-sm dark:border-outline-dark dark:bg-surface-dark-alt ${props.class ?? ""}`}
    >
      {/* Header */}
      <div class="grid grid-cols-[1fr_auto] items-center gap-4 p-4">
        <div class="flex flex-col gap-2">
          {/* Date */}
          <div class="h-3.5 w-2/3 animate-pulse rounded-radius bg-on-surface/30 dark:bg-on-surface-dark/30" />

          {/* Username */}
          <div class="h-4 w-1/2 animate-pulse rounded-radius bg-on-surface/30 dark:bg-on-surface-dark/30" />
        </div>

        {/* Avatar */}
        <div class="h-16 w-16 animate-pulse overflow-hidden rounded-full border border-outline dark:border-outline-dark bg-on-surface/30 dark:bg-on-surface-dark/30" />
      </div>

      {/* Travel Zones */}
      <div class="px-4 pb-4">
        <div class="rounded-radius bg-surface px-3 py-3 dark:bg-surface-dark">
          <div class="h-4 w-5/6 mx-auto animate-pulse rounded-radius bg-on-surface/30 dark:bg-on-surface-dark/30" />
        </div>
      </div>

      {/* Actions */}
      <div class="grid grid-cols-2 gap-3 border-t border-outline p-4 dark:border-outline-dark">
        <div class="h-12 w-full animate-pulse rounded-radius bg-primary/40 dark:bg-primary-dark/40" />
        <div class="h-12 w-full animate-pulse rounded-radius bg-primary/40 dark:bg-primary-dark/40" />
      </div>
    </article>
  );
};

export default BookingCardSkeleton;
