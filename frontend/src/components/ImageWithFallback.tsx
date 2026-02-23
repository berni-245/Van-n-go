import { JSX, Component, Suspense, splitProps } from "solid-js";
import ImgSkeleton from "@components/skeletons/ImgSkeleton";
import { createAsync, query } from "@solidjs/router";
import { useApi } from "../app-context/AppContext";

const imageQuery = query(async (src: string, fallbackSrc: string, skipSrc?: boolean) => {
  let res: Response;
  if (skipSrc) res = await fetch(fallbackSrc);
  else {
    res = await useApi().authFetch(src);
    if (!res.ok) {
      res = await fetch(fallbackSrc);
      if (!res.ok) throw new Error(`Failed to fetch default image '${fallbackSrc}'`);
    }
  }
  return res.blob();
}, "image-blob-with-fallback");

type ImageProps = {
  src: string;
  fallbackSrc: string;
  skipSrc?: boolean;
} & JSX.ImgHTMLAttributes<HTMLImageElement>;

const ImageWithFallback: Component<ImageProps> = (props) => {
  const blobSrc = createAsync(() =>
    imageQuery(props.src, props.fallbackSrc, props.skipSrc).then((blob) => {
      if (!blob) throw new Error("No blob!!!!");
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
