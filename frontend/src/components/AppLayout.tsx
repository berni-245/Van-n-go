import { children, ParentComponent } from "solid-js";

export const AppLayout: ParentComponent = (props) => {
  const resolvedChildren = children(() => props.children);
  return <div class="relative flex w-full flex-col md:flex-row">{resolvedChildren()}</div>;
};

export const Content: ParentComponent = (props) => {
  const resolvedChildren = children(() => props.children);
  return (
    <div id="main-content" class="h-svh w-full overflow-y-auto p-4 bg-white dark:bg-surface-dark">
      {resolvedChildren()}
    </div>
  );
};
