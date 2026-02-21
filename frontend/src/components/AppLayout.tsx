import { children, ParentComponent } from "solid-js";

const AppLayoutRoot: ParentComponent = (props) => {
  const resolvedChildren = children(() => props.children);
  return <div class="relative flex w-full flex-col md:flex-row">{resolvedChildren()}</div>;
};

const Content: ParentComponent = (props) => {
  const resolvedChildren = children(() => props.children);
  return (
    <main id="main-content" class="h-svh w-full overflow-y-auto p-4">
      {resolvedChildren()}
    </main>
  );
};

export const AppLayout = Object.assign(AppLayoutRoot, {
  Content,
});
