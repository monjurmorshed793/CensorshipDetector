/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CensorshipDetectorTestModule } from '../../../test.module';
import { WebAddressDetailComponent } from 'app/entities/web-address/web-address-detail.component';
import { WebAddress } from 'app/shared/model/web-address.model';

describe('Component Tests', () => {
    describe('WebAddress Management Detail Component', () => {
        let comp: WebAddressDetailComponent;
        let fixture: ComponentFixture<WebAddressDetailComponent>;
        const route = ({ data: of({ webAddress: new WebAddress(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [CensorshipDetectorTestModule],
                declarations: [WebAddressDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(WebAddressDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(WebAddressDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.webAddress).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
