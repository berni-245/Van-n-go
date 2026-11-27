import { children, JSX, ParentComponent } from "solid-js";
import { HtmlIdFamily } from "./htmlIds";

type SelectProps = {
  label?: string;
} & JSX.SelectHTMLAttributes<HTMLSelectElement>;

const Select: ParentComponent<SelectProps> = (props) => {
  const selectId = new HtmlIdFamily("select");
  const resolvedChildren = children(() => props.children);
  return (
    <div class="relative flex w-full max-w-xs flex-col gap-1 text-on-surface dark:text-on-surface-dark">
      <label for={selectId.id()} class="w-fit pl-0.5 text-sm">
        {props.label}
      </label>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 20 20"
        fill="currentColor"
        class="absolute pointer-events-none right-4 size-5"
        classList={{
          "top-[2rem]": !!props.label,
          "top-[0.7rem]": !props.label,
        }}
      >
        <path
          fill-rule="evenodd"
          d="M5.22 8.22a.75.75 0 0 1 1.06 0L10 11.94l3.72-3.72a.75.75 0 1 1 1.06 1.06l-4.25 4.25a.75.75 0 0 1-1.06 0L5.22 9.28a.75.75 0 0 1 0-1.06Z"
          clip-rule="evenodd"
        />
      </svg>
      <select
        id={selectId.id()}
        class="w-full appearance-none rounded-radius border border-outline bg-surface-alt px-4 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary disabled:cursor-not-allowed disabled:opacity-75 dark:border-outline-dark dark:bg-surface-dark-alt/50 dark:focus-visible:outline-primary-dark"
        {...props}
      >
        {resolvedChildren()}
      </select>
    </div>
  );
};

export default Select;
