import {Component} from "solid-js";
import ImgSkeleton from "@/components/skeletons/ImgSkeleton";

const Line = (props: { class?: string }) => (
    <div
        class={`h-4 rounded-md bg-on-surface/20 dark:bg-on-surface-dark/20 motion-safe:animate-pulse ${props.class ?? ""}`}
    />
);

const ButtonSkeleton = () => (
    <div class="h-10 w-32 rounded-radius bg-on-surface/20 dark:bg-on-surface-dark/20 motion-safe:animate-pulse"/>
);

const ProfilePageSkeleton: Component = () => {
    return (
        <div
            class="overflow-hidden rounded-radius border border-outline bg-surface-alt shadow-lg dark:border-outline-dark dark:bg-surface-dark-alt">
            {/* Header */}
            <div
                class="border-b border-outline bg-surface p-6 text-center dark:border-outline-dark dark:bg-surface-dark">
                <div
                    class="mx-auto h-8 w-48 rounded-md bg-on-surface/20 dark:bg-on-surface-dark/20 motion-safe:animate-pulse"/>
            </div>

            {/* Content */}
            <div class="p-8">
                {/* Avatar */}
                <div class="mb-8 flex flex-col items-center">
                    <ImgSkeleton circular class="h-40 w-40 border-4 border-outline dark:border-outline-dark"/>
                </div>

                {/* Info */}
                <div class="flex flex-col gap-4 text-center">
                    <Line class="mx-auto w-72"/>
                    <Line class="mx-auto w-64"/>
                    <Line class="mx-auto w-80"/>
                </div>

                {/* Buttons */}
                <div class="mt-10 flex flex-wrap justify-center gap-4">
                    <ButtonSkeleton/>
                    <ButtonSkeleton/>
                </div>
            </div>
        </div>
    );
};

export default ProfilePageSkeleton;
