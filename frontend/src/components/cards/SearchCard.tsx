import {Show} from "solid-js";
import {A} from "@solidjs/router";
import {Driver} from "@/api/types";
import ImageWithFallback from "@/components/ImageWithFallback";

import "./SearchCard.css";

interface SearchCardProps {
  driver: Driver;
  originZoneId?: string;
  from?: string;
}

export default function SearchCard(props: SearchCardProps) {
  const { driver, originZoneId } = props;

  const bookingHref = () =>
    originZoneId
      ? `/client/bookings/${driver.id}/new?originZoneId=${originZoneId}`
      : `/client/bookings/${driver.id}/new`;

  return (
    <li class="driver-card-wrapper">
      <A href={bookingHref()} class="driver-card">
        <div class="flex items-center gap-4 p-4">
          <div class="h-14 w-14 shrink-0 overflow-hidden rounded-full border border-outline dark:border-outline-dark">
            <ImageWithFallback
              src={driver.profilePic}
              fallbackSrc="/images/default-user.png"
              class="h-full w-full object-cover"
              alt={driver.username}
            />
          </div>
          <div class="min-w-0">
            <p class="truncate font-semibold text-on-surface-strong dark:text-on-surface-dark-strong">
              {driver.username}
            </p>
            <Show when={driver.rating != null}>
              <p class="text-sm text-on-surface dark:text-on-surface-dark">
                {"★".repeat(driver.rating!)}
                {"☆".repeat(5 - driver.rating!)}
              </p>
            </Show>
          </div>
        </div>
        <Show when={driver.description}>
          <p class="border-t border-outline px-4 py-3 text-sm text-on-surface dark:border-outline-dark dark:text-on-surface-dark line-clamp-3">
            {driver.description}
          </p>
        </Show>
      </A>
    </li>
  );
}
