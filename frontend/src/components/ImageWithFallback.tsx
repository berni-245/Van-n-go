import {Component, JSX, splitProps, Suspense} from "solid-js";
import ImgSkeleton from "@components/skeletons/ImgSkeleton";
import {createAsync, query} from "@solidjs/router";
import {useApi} from "../app-context/AppContext";

const imageQuery = query(async (src: string | undefined, fallbackSrc: string) => {
  if (src) {
    const res = await useApi().authFetch(src);
    if (res.ok) return res.blob();
  }
  const fallback = await fetch(fallbackSrc);
  if (!fallback.ok) throw new Error(`Failed to fetch default image '${fallbackSrc}'`);
  return fallback.blob();
}, "image-blob-with-fallback");

type ImageProps = {
  src?: string;
  fallbackSrc: string;
} & JSX.ImgHTMLAttributes<HTMLImageElement>;

const ImageWithFallback: Component<ImageProps> = (props) => {
  const blobSrc = createAsync(() =>
    imageQuery(props.src, props.fallbackSrc).then((blob) => {
      return URL.createObjectURL(blob);
    })
  );

  const [_, filteredProps] = splitProps(props, ["src", "class"]);

  const handleOnload = () => {
    const current = blobSrc();
    if (current) {
      URL.revokeObjectURL(current);
    } else {
      throw new Error("No blobSrc value to revoke");
    }
  };

  return (
    <div class="relative h-full w-full">
      <Suspense fallback={<ImgSkeleton class={props.class}/>}>
        <img src={blobSrc()} class={props.class} alt={props.alt} onLoad={handleOnload} {...filteredProps} />
      </Suspense>
    </div>
  );
};

export default ImageWithFallback;
