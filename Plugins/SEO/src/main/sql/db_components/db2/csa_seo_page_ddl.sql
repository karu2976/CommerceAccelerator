


--  @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/sql/ddlgen/csa_seo_page_ddl.xml#1 $$Change: 1385662 $

create table csa_seo_page (
	name	varchar(750)	not null,
	content	clob	not null
,constraint csa_seo_page_pk primary key (name));

commit;


