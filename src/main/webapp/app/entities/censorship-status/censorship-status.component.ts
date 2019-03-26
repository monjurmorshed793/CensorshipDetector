import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { merge, Observable, Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { ICensorshipStatus } from 'app/shared/model/censorship-status.model';
import { AccountService } from 'app/core';

import { ITEMS_PER_PAGE } from 'app/shared';
import { CensorshipStatusService } from './censorship-status.service';
import { IspService } from 'app/entities/isp';
import { IIsp, Isp } from 'app/shared/model/isp.model';
import { Select2OptionData } from 'ng-select2';
import { NgbTypeahead } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-censorship-status',
    templateUrl: './censorship-status.component.html'
})
export class CensorshipStatusComponent implements OnInit, OnDestroy {
    censorshipStatuses: ICensorshipStatus[];
    ispList: IIsp[];
    selectedIsp: IIsp;
    currentAccount: any;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    page: any;
    predicate: any;
    reverse: any;
    totalItems: number;
    currentSearch: string;
    ispMap: { [key: number]: IIsp };
    @ViewChild('instance') instance: NgbTypeahead;
    focus$ = new Subject<IIsp>();
    click$ = new Subject<IIsp>();

    constructor(
        protected censorshipStatusService: CensorshipStatusService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected parseLinks: JhiParseLinks,
        protected activatedRoute: ActivatedRoute,
        protected accountService: AccountService,
        protected ispService: IspService
    ) {
        this.censorshipStatuses = [];
        this.ispList = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 0;
        this.ispMap = {};
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    searchIsp = (text$: Observable<IIsp>) => {
        const debouncedText$ = text$.pipe(
            debounceTime(200),
            distinctUntilChanged()
        );
        const clicksWithClosedPopup$ = this.click$.pipe(filter(() => !this.instance.isPopupOpen()));
        const inputFocus$ = this.focus$;

        return merge(debouncedText$, inputFocus$, clicksWithClosedPopup$).pipe(
            map(term => (term === '' ? this.ispList : this.ispList.filter(v => v.id)).slice(0, 10))
        );
    };

    loadAllIspList() {
        this.ispService
            .query({
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<IIsp[]>) => {
                    this.ispList = res.body;
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    loadAll() {
        if (this.currentSearch) {
            this.censorshipStatusService
                .search({
                    query: this.currentSearch,
                    page: this.page,
                    size: this.itemsPerPage,
                    sort: this.sort()
                })
                .subscribe(
                    (res: HttpResponse<ICensorshipStatus[]>) => this.paginateCensorshipStatuses(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.censorshipStatusService
            .query({
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<ICensorshipStatus[]>) => this.paginateCensorshipStatuses(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
        this.loadAllIspList();
    }

    reset() {
        this.page = 0;
        this.censorshipStatuses = [];
        this.loadAll();
    }

    loadPage(page) {
        this.page = page;
        this.loadAll();
    }

    detectCensorship() {}

    clear() {
        this.censorshipStatuses = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch = '';
        this.loadAll();
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.censorshipStatuses = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = '_score';
        this.reverse = false;
        this.currentSearch = query;
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInCensorshipStatuses();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ICensorshipStatus) {
        return item.id;
    }

    registerChangeInCensorshipStatuses() {
        this.eventSubscriber = this.eventManager.subscribe('censorshipStatusListModification', response => this.reset());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    protected paginateCensorshipStatuses(data: ICensorshipStatus[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        for (let i = 0; i < data.length; i++) {
            this.censorshipStatuses.push(data[i]);
        }
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
