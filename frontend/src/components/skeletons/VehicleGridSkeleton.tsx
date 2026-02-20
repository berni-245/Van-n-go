import { Component, For, Ref } from "solid-js";
import ImgSkeleton from "./ImgSkeleton";

type Props = {
  ref?: Ref<HTMLDivElement>;
  class?: string;
};

const VehicleGridSkeleton: Component<Props> = (props) => {
  return (
    <div ref={props.ref} class={`grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 ${props.class ?? ""}`}>
      <For each={[1, 2, 3, 4, 5, 6]}>
        {() => (
          <div class="bg-surface-alt dark:bg-surface-dark-alt max-w-sm rounded-radius border border-outline dark:border-outline-dark">
            <div class="h-44 md:h-64 relative">
              <ImgSkeleton />
            </div>
            <div class="flex flex-col gap-4 p-6">
              {/* Plate number */}
              <div class="h-3.5 w-1/3 animate-pulse rounded-radius bg-on-surface/30 dark:bg-on-surface-dark/30" />

              {/* Size and Rate */}
              <div class="h-10 w-full animate-pulse rounded-radius bg-on-surface/30 dark:bg-on-surface-dark/30" />

              {/* Description */}
              <div class="h-3.5 w-full animate-pulse rounded-radius bg-on-surface/30 dark:bg-on-surface-dark/30" />
            </div>
          </div>
        )}
      </For>
    </div>
  );
};

export default VehicleGridSkeleton;
