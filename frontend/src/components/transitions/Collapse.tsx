import { Show, ParentComponent } from "solid-js";
import { Transition } from "solid-transition-group";

type CollapseProps = {
  expanded: () => boolean;
  // HACK: asking user for height shouldn't be necessary.
  height: number;
  duration?: number;
};

// NOTE: this is from the Transition gh page, they show a gif of it working,
// but at least in my case the opening animation is buggy... The problem seems
// to be the getHeight function returns 0 height onEnter.
export const Collapse: ParentComponent<CollapseProps> = (props) => {
  const duration = props.duration ?? 500;
  const COLLAPSED_PROPERTIES = {
    height: 0,
    marginTop: 0,
    marginBottom: 0,
    paddingTop: 0,
    paddingBottom: 0,
    borderTopWidth: 0,
    borderBottomWidth: 0,
  };

  function getHeight(el: Element): string {
    // const rect = el.getBoundingClientRect();
    // return `${rect.height}px`;
    return `${props.height}px`;
  }

  function onEnter(el: Element, done: VoidFunction) {
    const height = getHeight(el);
    // console.log("onEnter: ", height);
    const a = el.animate(
      [
        COLLAPSED_PROPERTIES,
        {
          height,
        },
      ],
      { duration, easing: "ease" }
    );

    a.finished.then(done);
  }

  function onExit(el: Element, done: VoidFunction) {
    const height = getHeight(el);
    // console.log("onExit: ", height);
    const a = el.animate(
      [
        {
          height,
        },
        COLLAPSED_PROPERTIES,
      ],
      { duration, easing: "ease" }
    );

    a.finished.then(done);
  }

  return (
    <Transition mode="outin" name="collapse" onEnter={onEnter} onExit={onExit}>
      <Show when={props.expanded()}>{props.children}</Show>
    </Transition>
  );
};

// export const Collapse: Component<CollapseProps> = (props) => {
//   return (
//     <Transition name="collapse">
//       <Show when={props.expanded()}>{props.children}</Show>
//     </Transition>
//   );
// };
