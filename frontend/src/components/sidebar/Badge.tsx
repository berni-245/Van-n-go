import { children, JSX, ParentComponent } from "solid-js";
import { ColorCategories } from "@components/uiTypes";

type BadgeProps = {
  category?: ColorCategories;
} & JSX.HTMLAttributes<HTMLSpanElement>;

const Badge: ParentComponent<BadgeProps> = (props) => {
  const resolvedChildren = children(() => props.children);
  return (
    <span class={`${BASE_CLASSES} ${TYPE_CLASSES[props.category || "alternate"]} ${props.class}`}>{resolvedChildren()}</span>
  );
};

export default Badge;

const BASE_CLASSES = "rounded-radius w-fit border px-2 py-1 text-xs font-medium";
const TYPE_CLASSES: Record<ColorCategories, string> = {
  primary:
    "border-primary bg-primary text-on-primary dark:border-primary-dark dark:bg-primary-dark dark:text-on-primary-dark",
  secondary:
    "border-secondary bg-secondary text-on-secondary dark:border-secondary-dark dark:bg-secondary-dark dark:text-on-secondary-dark",
  alternate:
    "border-outline bg-surface-alt text-on-surface dark:border-outline-dark dark:bg-surface-dark-alt dark:text-on-surface-dark",
  inverse:
    "border-outline-dark bg-surface-dark-alt text-on-surface-dark dark:border-outline dark:bg-surface-alt dark:text-on-surface",
  info: "border-info bg-info text-on-info dark:border-info dark:bg-info dark:text-on-info",
  danger: "border-danger bg-danger text-on-danger dark:border-danger dark:bg-danger dark:text-on-danger",
  warning: "border-warning bg-warning text-on-warning dark:border-warning dark:bg-warning dark:text-on-warning",
  success: "border-success bg-success text-on-success dark:border-success dark:bg-success dark:text-on-success",
};

