import { Component, JSX, Show } from "solid-js";
import { ColorCategories } from "@/components/uiTypes";
import { A } from "@solidjs/router";

type ButtonProps = {
  icon?: JSX.Element;
  text?: string;
  onClick?: (event: Event) => void;
  href?: string;
  type?: JSX.ButtonHTMLAttributes<HTMLButtonElement>["type"];
  category: ColorCategories;
  class?: string;
  disabled?: boolean;
};

const Button: Component<ButtonProps> = (props) => {
  return (
    <>
      <Show when={props.href}>
        <A
          type="button"
          href={props.href!}
          onClick={props.onClick}
          class={`${BASE_CLASSES} ${TYPE_CLASSES[props.category]} ${props.class}`}
        >
          {props.icon}
          {props.text}
        </A>
      </Show>
      <Show when={!props.href}>
        <button
          type={props.type || "button"}
          onClick={props.onClick}
          class={`${BASE_CLASSES} ${TYPE_CLASSES[props.category]}`}
          disabled={props.disabled}
        >
          {props.icon}
          {props.text}
        </button>
      </Show>
    </>
  );
};

export default Button;

const BASE_CLASSES =
  "inline-flex justify-center items-center gap-2 whitespace-nowrap rounded-radius border px-4 py-2 font-medium tracking-wide transition hover:opacity-75 text-center focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 active:opacity-100 active:outline-offset-0 disabled:opacity-75 disabled:cursor-not-allowed";
const TYPE_CLASSES: Record<ColorCategories, string> = {
  primary:
    "bg-primary border-primary dark:border-primary-dark text-on-primary focus-visible:outline-primary dark:bg-primary-dark dark:text-on-primary-dark dark:focus-visible:outline-primary-dark",
  secondary:
    "bg-secondary border-secondary dark:border-secondary-dark text-on-secondary focus-visible:outline-secondary dark:bg-secondary-dark dark:text-on-secondary-dark dark:focus-visible:outline-secondary-dark",
  alternate:
    "bg-surface-alt border-surface-alt dark:border-surface-dark-alt text-on-surface-strong focus-visible:outline-surface-alt dark:bg-surface-dark-alt dark:text-on-surface-dark-strong dark:focus-visible:outline-surface-dark-alt",
  inverse:
    "bg-surface-dark border-surface-dark dark:border-surface text-on-surface-dark focus-visible:outline-surface-dark dark:bg-surface dark:text-on-surface dark:focus-visible:outline-surface",
  info: "bg-info border-info dark:border-info text-on-info focus-visible:outline-info dark:bg-info dark:text-on-info dark:focus-visible:outline-info",
  danger:
    "bg-danger border-danger dark:border-danger text-on-danger focus-visible:outline-danger dark:bg-danger dark:text-on-danger dark:focus-visible:outline-danger",
  warning:
    "bg-warning border-warning dark:border-warning text-on-warning focus-visible:outline-warning dark:bg-warning dark:text-on-warning dark:focus-visible:outline-warning",
  success:
    "bg-success border-success dark:border-success text-on-success focus-visible:outline-success dark:bg-success dark:text-on-success dark:focus-visible:outline-success",
};
