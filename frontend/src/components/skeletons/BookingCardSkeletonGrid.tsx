import { Component, For } from "solid-js";
import BookingCardSkeleton from "./BookingCardSkeleton";

const BookingCardSkeletonGrid: Component<{ class?: string }> = (props) => {
  return (
    <div class={`grid gap-6 grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 ${props.class ?? ""}`}>
      <For each={[1, 2, 3, 4, 5, 6]}>{() => <BookingCardSkeleton />}</For>
    </div>
  );
};

export default BookingCardSkeletonGrid;
