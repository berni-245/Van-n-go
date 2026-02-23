import { Component, createSignal, For, Show } from "solid-js";

type StarsProps = {
  name: string;
  label?: string;
  error?: string | string[];
  initialValue?: number;
};

const Stars: Component<StarsProps> = (props) => {
  const [rating, setRating] = createSignal(props.initialValue || 0);


  const stars = [1, 2, 3, 4, 5];

  return (
    <div class="flex flex-col gap-1 text-on-surface dark:text-on-surface-dark">
      <Show when={props.label}>
        <span class="w-fit pl-0.5 text-sm" classList={{ "text-danger": !!props.error }}>
          {props.label}
        </span>
      </Show>

      <div class="flex items-center gap-1">
        <For each={stars}>
          {(star) => (
            <label class="cursor-pointer transition hover:scale-125 focus-within:scale-125">
              <span class="sr-only">{star} estrellas</span>

              <input
                type="radio"
                name={props.name}
                value={star}
                class="sr-only"
                checked={rating() === star}
                onChange={() => setRating(star)}
              />


              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="currentColor"
                class="size-8 transition-colors"
                classList={{
                  "text-amber-500": rating() >= star,
                  "text-on-surface/30 dark:text-on-surface-dark/30": rating() < star,
                }}
              >
                <path
                  fill-rule="evenodd"
                  d="M10.788 3.21c.448-1.077 1.976-1.077 2.424 0l2.082 5.006 5.404.434c1.164.093 1.636 1.545.749 2.305l-4.117 3.527 1.257 5.273c.271 1.136-.964 2.033-1.96 1.425L12 18.354 7.373 21.18c-.996.608-2.231-.29-1.96-1.425l1.257-5.273-4.117-3.527c-.887-.76-.415-2.212.749-2.305l5.404-.434 2.082-5.005Z"
                  clip-rule="evenodd"
                />
              </svg>
            </label>
          )}
        </For>
      </div>


      <Show when={props.error}>
        <div class="flex flex-col gap-0.5">
          <Show
            when={Array.isArray(props.error)}
            fallback={<small class="pl-0.5 text-danger">{props.error as string}</small>}
          >
            <For each={props.error as string[]}>
              {(err) => <small class="pl-0.5 text-danger">• {err}</small>}
            </For>
          </Show>
        </div>
      </Show>
    </div>
  );
};

export default Stars;