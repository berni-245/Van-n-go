import { Component } from "solid-js";
import Button from "@components/Button";

type CardProps = {
  img: string;
  location?: string;
  title: string;
  description: string;
  buttonText?: string;
  onButtonClick?: (event: Event) => void;
};

const Card: Component<CardProps> = (props) => {
  return (
    <>
      <article class="group flex rounded-radius max-w-sm flex-col overflow-hidden border border-outline bg-surface-alt text-on-surface dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark">
        <div class="h-44 md:h-64 overflow-hidden">
          <img
            src={props.img}
            class="object-cover transition duration-700 ease-out group-hover:scale-105"
            alt="view of a coastal Mediterranean village on a hillside, with small boats in the water."
          />
        </div>
        <div class="flex flex-col gap-4 p-6">
          <div class="flex items-center gap-1 font-medium">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
              stroke-width="1.5"
              stroke="currentColor"
              aria-hidden="true"
              class="size-5"
            >
              <path stroke-linecap="round" stroke-linejoin="round" d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z"
              />
            </svg>
            <span>{props.location}</span>
          </div>
          <h3
            class="text-balance text-xl lg:text-2xl font-bold text-on-surface-strong dark:text-on-surface-dark-strong"
            aria-describedby="tripDescription"
          >
            {props.title}
          </h3>
          <p id="tripDescription" class="text-pretty mb-2">
            {props.description}
          </p>
          <Button category="primary" text={props.buttonText} onClick={props.onButtonClick} />
        </div>
      </article>
    </>
  );
};

export default Card;
